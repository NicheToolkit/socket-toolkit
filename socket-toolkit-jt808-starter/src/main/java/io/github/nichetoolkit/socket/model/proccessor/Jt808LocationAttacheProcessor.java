package io.github.nichetoolkit.socket.model.proccessor;

/**
 * <p>LocationAttacheProcessor</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 * @company 郑州高维空间技术有限公司(c) 2021-2022
 * @date created on 14:07 2021/11/17
 */
public interface Jt808LocationAttacheProcessor {
    /**
     * 定位附属信息解析处理
     */
    void process(int id, byte[] bytes);
}
