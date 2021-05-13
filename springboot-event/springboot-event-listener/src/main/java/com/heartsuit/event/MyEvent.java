package com.heartsuit.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * Author:  Heartsuit
 * Date:  2021/5/11 17:10
 */
@Data
public class MyEvent extends ApplicationEvent {
    private String msg;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public MyEvent(Object source) {
        super(source);
    }

    public MyEvent(Object source, String msg) {
        super(source);
        this.msg = msg;
    }
}
