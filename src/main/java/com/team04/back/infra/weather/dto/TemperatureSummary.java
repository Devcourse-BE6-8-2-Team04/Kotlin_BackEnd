package com.team04.back.infra.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureSummary {
    private double min;
    private double max;
    private double afternoon;
    private double night;
    private double evening;
    private double morning;
}
