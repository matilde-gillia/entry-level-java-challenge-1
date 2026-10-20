package com.challenge.api.service;

import com.challenge.api.model.CreateEmployeeRequest;
import com.challenge.api.model.Employee;
import com.challenge.api.model.EmployeeRecord;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);
    private final Map<UUID, Employee> employees = new ConcurrentHashMap<>();

    public EmployeeService() {
        addMockEmployee("Carlota", "Miller", 120000, 36, "Principal Engineer", "carlota@example.com");
        addMockEmployee("Alexa", "Torres", 110000, 40, "Engineering Manager", "alexa@example.com");
    }

    public List<Employee> getAllEmployees() {
        return List.copyOf(employees.values());
    }

    public Employee getEmployeeByUuid(UUID uuid) {
        Employee employee = employees.get(uuid);
        if (employee == null) {
            log.warn("Employee lookup failed for uuid={}", uuid);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
        }
        log.debug("Employee lookup succeeded for uuid={}", uuid);
        return employee;
    }

    public Employee createEmployee(CreateEmployeeRequest request) {
        validate(request);

        EmployeeRecord employee = new EmployeeRecord(
                request.getFirstName(),
                request.getLastName(),
                request.getSalary(),
                request.getAge(),
                request.getJobTitle(),
                request.getEmail(),
                request.getContractHireDate(),
                request.getContractTerminationDate());
        employees.put(employee.getUuid(), employee);
        log.info("Employee created with uuid={}", employee.getUuid());
        return employee;
    }

    public long countEmployees() {
        return employees.size();
    }

    private void validate(CreateEmployeeRequest request) {
        if (request == null
                || isBlank(request.getFirstName())
                || isBlank(request.getLastName())
                || request.getSalary() == null
                || request.getSalary() < 0
                || request.getAge() == null
                || request.getAge() < 0
                || isBlank(request.getJobTitle())
                || isBlank(request.getEmail())
                || request.getContractHireDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid employee request");
        }

        if (request.getContractTerminationDate() != null
                && request.getContractTerminationDate().isBefore(request.getContractHireDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Termination date precedes hire date");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void addMockEmployee(
            String firstName, String lastName, Integer salary, Integer age, String jobTitle, String email) {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setSalary(salary);
        request.setAge(age);
        request.setJobTitle(jobTitle);
        request.setEmail(email);
        request.setContractHireDate(Instant.parse("2020-01-01T00:00:00Z"));
        createEmployee(request);
    }
}
