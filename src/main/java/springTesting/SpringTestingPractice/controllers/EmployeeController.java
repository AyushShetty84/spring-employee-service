package springTesting.SpringTestingPractice.controllers;

import lombok.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springTesting.SpringTestingPractice.dto.EmployeeDto;
import springTesting.SpringTestingPractice.services.EmployeeService;

@RestController
@RequestMapping({"/employees"})
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping({"/{id}"})
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        EmployeeDto employeeDto = this.employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employeeDto);
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> createNewEmployee(@RequestBody EmployeeDto employeeDto) {
        EmployeeDto createdEmployeeDto = this.employeeService.createNewEmployee(employeeDto);
        return new ResponseEntity(createdEmployeeDto, HttpStatus.CREATED);
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDto employeeDto) {
        EmployeeDto updatedEmployee = this.employeeService.updateEmployee(id, employeeDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        this.employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @Generated
    public EmployeeController(final EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
}