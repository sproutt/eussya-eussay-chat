package com.sproutt.eussyaeussyachat.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OneToOneChatMessageDTO {
    private long from;
    private long to;
    private String content;
}
