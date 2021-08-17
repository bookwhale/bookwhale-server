package com.teamherb.bookstoreback.common.global;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class GlobalVariable {

    private Map<String, Object> variables = new HashMap<>();

    @PostConstruct
    public void initialize() {

    }

}
