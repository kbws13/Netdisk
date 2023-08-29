package xyz.kbws.entity.dto;

import java.io.Serializable;

public class UserSpaceDto implements Serializable {
    private Long userSpace;
    private Long totalSpace;

    public Long getUserSpace() {
        return userSpace;
    }

    public void setUserSpace(Long userSpace) {
        this.userSpace = userSpace;
    }

    public Long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
    }
}
