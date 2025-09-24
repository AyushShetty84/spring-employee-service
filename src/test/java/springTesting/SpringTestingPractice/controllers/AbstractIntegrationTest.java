package springTesting.SpringTestingPractice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import springTesting.SpringTestingPractice.TestContainerConfiguration;
import springTesting.SpringTestingPractice.dto.EmployeeDto;
import springTesting.SpringTestingPractice.entities.Employee;

@AutoConfigureWebTestClient(
        timeout = "100000"
)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Import({TestContainerConfiguration.class})
public class AbstractIntegrationTest {
    @Autowired
    WebTestClient webTestClient;
    Employee testEmployee = Employee.builder().email("anuj@gmail.com").fullName("Anuj").salary(200L).build();
    EmployeeDto testEmployeeDto = EmployeeDto.builder().email("anuj@gmail.com").fullName("Anuj").salary(200L).build();
}
