package com.budget.tracker.controller;

import com.budget.tracker.model.Expense;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final Map<Long, Expense> expenseRepo = new HashMap<>();
    private long idCounter = 1;

    @PostMapping
    public ResponseEntity<Expense> addExpense(@Valid @RequestBody Expense expense) {
        expense.setId(idCounter++);
        expenseRepo.put(expense.getId(), expense);
        return new ResponseEntity<>(expense, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenseRepo.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        Expense expense = expenseRepo.get(id);
        if (expense == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(expense);
    }
}