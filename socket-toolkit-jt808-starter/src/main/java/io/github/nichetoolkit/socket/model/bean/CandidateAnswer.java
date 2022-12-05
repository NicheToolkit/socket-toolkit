package io.github.nichetoolkit.socket.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>CandidateAnswer 候选答案 </p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
@Builder
@Setter
@Getter
public class CandidateAnswer {
    private byte id;
    private byte[] length;
    private byte[] content;
}
