package springTesting.SpringTestingPractice.services.impl;

import java.util.List;
import lombok.Generated;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import springTesting.SpringTestingPractice.dto.EmployeeDto;
import springTesting.SpringTestingPractice.entities.Employee;
import springTesting.SpringTestingPractice.exceptions.ResourceNotFoundException;
import springTesting.SpringTestingPractice.repositories.EmployeeRepository;
import springTesting.SpringTestingPractice.services.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    public EmployeeDto getEmployeeById(Long id) {
        log.info("Fetching employee with id: {}", id);
        Employee employee = (Employee)this.employeeRepository.findById(id).orElseThrow(() -> {
            log.error("Employee not found with id: {}", id);
            return new ResourceNotFoundException("Employee not found with id: " + id);
        });
        log.info("Successfully fetched employee with id: {}", id);
        return (EmployeeDto)this.modelMapper.map(employee, EmployeeDto.class);
    }

    public EmployeeDto createNewEmployee(EmployeeDto employeeDto) {
        log.info("Creating new employee with email: {}", employeeDto.getEmail());
        List<Employee> existingEmployees = this.employeeRepository.findByEmail(employeeDto.getEmail());
        if (!existingEmployees.isEmpty()) {
            log.error("Employee already exists with email: {}", employeeDto.getEmail());
            throw new RuntimeException("Employee already exists with email: " + employeeDto.getEmail());
        } else {
            Employee newEmployee = (Employee)this.modelMapper.map(employeeDto, Employee.class);
            Employee savedEmployee = (Employee)this.employeeRepository.save(newEmployee);
            log.info("Successfully created new employee with id: {}", savedEmployee.getId());
            return (EmployeeDto)this.modelMapper.map(savedEmployee, EmployeeDto.class);
        }
    }

    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto) {
        log.info("Updating employee with id: {}", id);
        Employee employee = (Employee)this.employeeRepository.findById(id).orElseThrow(() -> {
            log.error("Employee not found with id: {}", id);
            return new ResourceNotFoundException("Employee not found with id: " + id);
        });
        if (!employee.getEmail().equals(employeeDto.getEmail())) {
            log.error("Attempted to update email for employee with id: {}", id);
            throw new RuntimeException("The email of the employee cannot be updated");
        } else {
            this.modelMapper.map(employeeDto, employee);
            employee.setId(id);
            Employee savedEmployee = (Employee)this.employeeRepository.save(employee);
            log.info("Successfully updated employee with id: {}", id);
            return (EmployeeDto)this.modelMapper.map(savedEmployee, EmployeeDto.class);
        }
    }

    public void deleteEmployee(Long id) {
        log.info("Deleting employee with id: {}", id);
        boolean exists = this.employeeRepository.existsById(id);
        if (!exists) {
            log.error("Employee not found with id: {}", id);
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        } else {
            this.employeeRepository.deleteById(id);
            log.info("Successfully deleted employee with id: {}", id);
        }
    }

    @Generated
    public EmployeeServiceImpl(final EmployeeRepository employeeRepository, final ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }
}
