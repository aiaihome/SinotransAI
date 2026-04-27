package com.sinotrans.blankbill.service;

import com.sinotrans.blankbill.dto.BlankBillSegmentRequest;
import com.sinotrans.blankbill.dto.BlankBillSegmentResponse;
import com.sinotrans.blankbill.dto.BlankBillSegmentArchiveResponse;
import com.sinotrans.blankbill.dto.BlankBillSegmentCheckRequest;
import com.sinotrans.blankbill.dto.BlankBillSegmentCheckResponse;
import com.sinotrans.blankbill.dto.BlankBillSegmentEditRequest;
import com.sinotrans.blankbill.dto.BlankBillSegmentEditResponse;
import com.sinotrans.blankbill.dto.BlankBillSegmentListQuery;
import com.sinotrans.blankbill.dto.BlankBillSegmentListResponse;

public interface BlankBillSegmentService {
    BlankBillSegmentResponse createSegment(BlankBillSegmentRequest request);
    BlankBillSegmentCheckResponse checkSegment(BlankBillSegmentCheckRequest request);
    BlankBillSegmentEditResponse editSegment(Long segmentId, BlankBillSegmentEditRequest request);
    BlankBillSegmentArchiveResponse archiveSegments();
    BlankBillSegmentListResponse getSegments(BlankBillSegmentListQuery query);
}
