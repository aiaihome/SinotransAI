package com.sinotrans.blankbill.dto;

import lombok.Data;
import java.util.List;

@Data
public class BlankBillSegmentArchiveResponse {
    private boolean success;
    private String message;
    private DataObj data;

    @lombok.Data
    public static class DataObj {
        private Integer archiveCount;
        private List<Long> archivedSegmentIds;
    }
}
