package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Liyajie
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person implements Serializable {
    private static final long serialVersionUID = -55775790876677L;
    private int id;
    private String name;
    private int age;

    @Override
    public String toString() {
        return id + "::" + name + "::" + age;
    }
}
