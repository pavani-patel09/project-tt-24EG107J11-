package com.medikiosk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class DocumentDTOs {

    public static class VerifiedFieldItem {
        @JsonProperty("field_name")
        private String fieldName;
        @JsonProperty("corrected_value")
        private String correctedValue;

        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        public String getCorrectedValue() { return correctedValue; }
        public void setCorrectedValue(String correctedValue) { this.correctedValue = correctedValue; }
    }

    public static class DocumentVerifyRequest {
        @JsonProperty("document_id")
        private Long documentId;
        private List<VerifiedFieldItem> fields;

        public Long getDocumentId() { return documentId; }
        public void setDocumentId(Long documentId) { this.documentId = documentId; }
        public List<VerifiedFieldItem> getFields() { return fields; }
        public void setFields(List<VerifiedFieldItem> fields) { this.fields = fields; }
    }
}
