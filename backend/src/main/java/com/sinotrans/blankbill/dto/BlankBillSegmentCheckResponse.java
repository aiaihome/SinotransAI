package com.sinotrans.blankbill.dto;

import lombok.Data;
import java.util.List;

@Data
public class BlankBillSegmentCheckResponse {
    private boolean conflict;
    private List<ConflictSegment> conflictSegments;
    private String message;

    @lombok.Data
    public static class ConflictSegment {
        private Long segmentId;
        private String startNumber;
        private String endNumber;
    }
}
