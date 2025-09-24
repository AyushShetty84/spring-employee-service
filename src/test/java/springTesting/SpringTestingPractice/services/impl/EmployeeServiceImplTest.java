package springTesting.SpringTestingPractice.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import springTesting.SpringTestingPractice.TestContainerConfiguration;
import springTesting.SpringTestingPractice.dto.EmployeeDto;
import springTesting.SpringTestingPractice.entities.Employee;
import springTesting.SpringTestingPractice.exceptions.ResourceNotFoundException;
import springTesting.SpringTestingPractice.repositories.EmployeeRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@Import(value = {TestContainerConfiguration.class})
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;
    private Employee mockEmployee;
    private EmployeeDto mockEmployeeDto;

    @BeforeEach
    void setUp() {
        this.mockEmployee = Employee.builder().id(1L).email("anuj@gmail.com").fullName("Anuj").salary(200L).build();
        this.mockEmployeeDto = (EmployeeDto)this.modelMapper.map(this.mockEmployee, EmployeeDto.class);
    }

    @Test
    void testGetEmployeeById_WhenEmployeeIdIsPresent_ThenReturnEmployeeDto(){

        Long id = this.mockEmployee.getId();

        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee));

        EmployeeDto employeeDto = employeeService.getEmployeeById(id);

        Assertions.assertThat(employeeDto).isNotNull();
        Assertions.assertThat(employeeDto.getId()).isEqualTo(id);
        Assertions.assertThat(employeeDto.getEmail()).isEqualTo(this.mockEmployee.getEmail());

        verify(employeeRepository).findById(id);
    }

    @Test
    void testGetEmployeeById_WhenEmployeeIdIsNotPresent_ThenThrowException(){

        Mockito.when(employeeRepository.findById(mockEmployee.getId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(()->employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: " + mockEmployee.getId());

        verify(employeeRepository).findById(1L);
    }

    @Test
    void testCreateEmployee_whenValidEmployee_thenCreateNewEmployee(){

        Mockito.when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(this.mockEmployee);


        EmployeeDto employeeDto = employeeService.createNewEmployee(this.mockEmployeeDto);

        Assertions.assertThat(employeeDto).isNotNull();
        Assertions.assertThat(employeeDto.getEmail()).isEqualTo(this.mockEmployeeDto.getEmail());

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);

        verify(employeeRepository).save(employeeArgumentCaptor.capture());

        Employee employeeCaptured = employeeArgumentCaptor.getValue();
        Assertions.assertThat(employeeCaptured.getEmail()).isEqualTo(this.mockEmployee.getEmail());
    }


    @Test
    void testCreateEmployee_whenExistingEmployee_thenThrowException(){

        Mockito.when(employeeRepository.findByEmail(mockEmployeeDto.getEmail())).thenReturn(List.of(mockEmployee));

        Assertions.assertThatThrownBy(()->employeeService.createNewEmployee(mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: " + mockEmployeeDto.getEmail());

        verify(employeeRepository).findByEmail(mockEmployee.getEmail());
    }

    @Test
    void testupdateEmployee_whenEmployeeIdDoesNotExists_ThenReturnException(){

        Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(()->employeeService.updateEmployee(1L,mockEmployeeDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: " + mockEmployeeDto.getId());

        verify(employeeRepository).findById(1L);
        verify(employeeRepository,never()).save(mockEmployee);
    }

    @Test
    void testUpdateEmployee_whenUpdateEmployeeEmail_thenThrowException(){
        Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        mockEmployeeDto.setEmail("random@gmail.com");

        Assertions.assertThatThrownBy(()->employeeService.updateEmployee(mockEmployeeDto.getId(),mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");

        verify(employeeRepository).findById(mockEmployeeDto.getId());
        verify(employeeRepository,never()).save(mockEmployee);
    }

    @Test
    void testUpdateEmployee_whenValidEmployee_thenUpdateEmployee() {
//        arrange
        when(employeeRepository.findById(mockEmployeeDto.getId())).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDto.setFullName("Random name");
        mockEmployeeDto.setSalary(199L);


        Employee newEmployee = modelMapper.map(mockEmployeeDto, Employee.class);
        when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);
//        act
        EmployeeDto updatedEmployeeDto = employeeService.updateEmployee(mockEmployeeDto.getId(), mockEmployeeDto);

        assertThat(updatedEmployeeDto).isEqualTo(mockEmployeeDto);

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any());
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        Mockito.when(this.employeeRepository.existsById(1L)).thenReturn(false);
        ((AbstractThrowableAssert)Assertions.assertThatThrownBy(() -> this.employeeService.deleteEmployee(1L)).isInstanceOf(ResourceNotFoundException.class)).hasMessage("Employee not found with id: 1");
        ((EmployeeRepository)Mockito.verify(this.employeeRepository, Mockito.never())).deleteById(Mockito.anyLong());
    }

    @Test
    void testDeleteEmployee_whenEmployeeIsValid_thenDeleteEmployee() {
        Mockito.when(this.employeeRepository.existsById(1L)).thenReturn(true);
        Assertions.assertThatCode(() -> this.employeeService.deleteEmployee(1L)).doesNotThrowAnyException();
        ((EmployeeRepository)Mockito.verify(this.employeeRepository)).deleteById(1L);
    }

}