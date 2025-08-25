package com.team04.back.infra.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyFeelsLike {
    private double day;
    private double night;
    private double eve;
    private double morn;
}
