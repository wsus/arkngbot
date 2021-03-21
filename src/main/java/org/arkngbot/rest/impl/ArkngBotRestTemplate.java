package org.arkngbot.rest.impl;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class ArkngBotRestTemplate extends RestTemplate {

    public ArkngBotRestTemplate() {
        super();
        // use a custom the Jackson mapper to ignore case because capitalized names are used in the JSON
        ObjectMapper mapper = JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .build();
        HttpMessageConverter<Object> messageConverter = new MappingJackson2HttpMessageConverter(mapper);

        setMessageConverters(Collections.singletonList(messageConverter));
    }
}
