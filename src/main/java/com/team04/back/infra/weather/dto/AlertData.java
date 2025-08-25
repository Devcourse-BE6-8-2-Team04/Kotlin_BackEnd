package com.team04.back.infra.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertData {
    @JsonProperty("sender_name")
    private String senderName;
    private String event;
    private long start;
    private long end;
    private String description;
    private List<String> tags;
}
