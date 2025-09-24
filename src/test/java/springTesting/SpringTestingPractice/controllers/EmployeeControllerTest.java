package springTesting.SpringTestingPractice.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import springTesting.SpringTestingPractice.TestContainerConfiguration;
import springTesting.SpringTestingPractice.dto.EmployeeDto;
import springTesting.SpringTestingPractice.entities.Employee;
import springTesting.SpringTestingPractice.repositories.EmployeeRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureWebTestClient(
        timeout = "100000"
)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Import(value = {TestContainerConfiguration.class})




class EmployeeControllerTest extends AbstractIntegrationTest{


        @Autowired
        private WebTestClient webTestClient;

        @Autowired
        private EmployeeRepository employeeRepository;

        private Employee testemployee;

        private EmployeeDto testemployeeDto;

        @BeforeEach
        void setUp() {
        this.employeeRepository.deleteAll();
    }

    @Test
    void testGetEmployeeById_success() {
        Employee savedEmployee = employeeRepository.save(this.testEmployee);

        webTestClient.get()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedEmployee.getId())
                .jsonPath("$.email").isEqualTo(savedEmployee.getEmail())
                .jsonPath("$.fullName").isEqualTo(savedEmployee.getFullName())
                .jsonPath("$.salary").isEqualTo(savedEmployee.getSalary());
    }


    @Test
    void testGetEmployeeById_Failure() {
        webTestClient.get()
                .uri("/employees/999") // make sure this ID does not exist
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeAlreadyExists_thenThrowException() {
        // Save first employee
        employeeRepository.save(this.testEmployee);

        // Try to create duplicate
        webTestClient.post()
                .uri("/employees")
                .bodyValue(this.testEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeDoesNotExists_thenCreateEmployee() {
        this.webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(testEmployeeDto.getEmail())
                .jsonPath("$.fullName").isEqualTo(testEmployeeDto.getFullName());
    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        ((WebTestClient.RequestBodySpec)this.webTestClient.put().uri("/employees/999", new Object[0])).bodyValue(this.testEmployeeDto).exchange().expectStatus().isNotFound();
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateTheEmail_thenThrowException() {
        Employee savedEmployee = (Employee)this.employeeRepository.save(this.testEmployee);
        this.testEmployeeDto.setFullName("Random Name");
        this.testEmployeeDto.setEmail("random@gmail.com");
        ((WebTestClient.RequestBodySpec)this.webTestClient.put().uri("/employees/{id}", new Object[]{savedEmployee.getId()})).bodyValue(this.testEmployeeDto).exchange().expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateEmployee_whenEmployeeIsValid_thenUpdateEmployee() {
        Employee savedEmployee = employeeRepository.save(this.testEmployee);

        // Update DTO with new values
        this.testEmployeeDto.setId(savedEmployee.getId()); // ensure ID matches
        this.testEmployeeDto.setFullName("Random Name");
        this.testEmployeeDto.setSalary(250L);

        webTestClient.put()
                .uri("/employees/{id}", savedEmployee.getId())
                .bodyValue(this.testEmployeeDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .consumeWith(response -> {
                    EmployeeDto body = response.getResponseBody();
                    assertNotNull(body);
                    assertEquals(this.testEmployeeDto.getId(), body.getId());
                    assertEquals(this.testEmployeeDto.getFullName(), body.getFullName());
                    assertEquals(this.testEmployeeDto.getEmail(), body.getEmail());
                    assertEquals(this.testEmployeeDto.getSalary(), body.getSalary());
                });
    }


    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        this.webTestClient.delete().uri("/employees/1", new Object[0]).exchange().expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployee_whenEmployeeExists_thenDeleteEmployee() {
        Employee savedEmployee = (Employee)this.employeeRepository.save(this.testEmployee);
        this.webTestClient.delete().uri("/employees/{id}", new Object[]{savedEmployee.getId()}).exchange().expectStatus().isNoContent().expectBody(Void.class);
        this.webTestClient.delete().uri("/employees/{id}", new Object[]{savedEmployee.getId()}).exchange().expectStatus().isNotFound();
    }
}
