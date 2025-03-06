package com.Pogoy.Activity;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    @PatchMapping("/{id}")
    public ResponseEntity<String> testPatch(@PathVariable String id, @RequestBody Map<String, String> updates) {
        System.out.println("🚀 PATCH request received for ID: " + id);
        return ResponseEntity.ok("PATCH received for ID: " + id);
    }
}

