package springTesting.SpringTestingPractice.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import springTesting.SpringTestingPractice.entities.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByEmail(String email);
}
