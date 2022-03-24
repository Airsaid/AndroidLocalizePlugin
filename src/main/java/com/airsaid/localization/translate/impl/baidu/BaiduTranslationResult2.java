package com.airsaid.localization.translate.impl.baidu;

import java.util.List;

public class BaiduTranslationResult2 {

    private ResultDTO result;
    private Long log_id;

    public ResultDTO getResult() {
        return result;
    }

    public void setResult(ResultDTO result) {
        this.result = result;
    }

    public Long getLog_id() {
        return log_id;
    }

    public void setLog_id(Long log_id) {
        this.log_id = log_id;
    }

    public boolean isSuccess() {
        return result != null;
    }

    public static class ResultDTO {
        private String from;
        private List<TransResultDTO> trans_result;
        private String to;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public List<TransResultDTO> getTrans_result() {
            return trans_result;
        }

        public void setTrans_result(List<TransResultDTO> trans_result) {
            this.trans_result = trans_result;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public static class TransResultDTO {
            private String dst;
            private String src;

            public String getDst() {
                return dst;
            }

            public void setDst(String dst) {
                this.dst = dst;
            }

            public String getSrc() {
                return src;
            }

            public void setSrc(String src) {
                this.src = src;
            }
        }
    }
}
