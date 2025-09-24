package springTesting.SpringTestingPractice;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import springTesting.SpringTestingPractice.entities.Employee;
import springTesting.SpringTestingPractice.repositories.EmployeeRepository;

import java.util.List;


@Slf4j
@Import(value = {TestContainerConfiguration.class})
@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class SpringTestingPracticeApplicationTests {

    @Autowired
    private EmployeeRepository employeeRepository;
    private Employee employee;

    @BeforeEach
    void setUp() {

        this.employee = Employee.builder().fullName("Anuj").email("anuj@gmail.com").salary(100L).build();
    }

    @Test
    void testFindByEmail_whenEmailIsPresent_thenReturnEmployee() {
        this.employeeRepository.save(this.employee);
        List<Employee> employeeList = this.employeeRepository.findByEmail(this.employee.getEmail());
        Assertions.assertThat(employeeList).isNotNull();
        Assertions.assertThat(employeeList).isNotEmpty();
        Assertions.assertThat(employeeList.get(0).getEmail()).isEqualTo(this.employee.getEmail());
    }

    @Test
    void testFindByEmail_whenEmailIsNotFound_thenReturnEmptyEmployeeList() {
        String email = "notPresent.123@gmail.com";
        List<Employee> employeeList = this.employeeRepository.findByEmail(email);
        Assertions.assertThat(employeeList).isNotNull();
        Assertions.assertThat(employeeList).isEmpty();
    }


}
