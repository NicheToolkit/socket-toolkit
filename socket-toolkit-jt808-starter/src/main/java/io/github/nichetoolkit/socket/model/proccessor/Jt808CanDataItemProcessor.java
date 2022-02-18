package io.github.nichetoolkit.socket.model.proccessor;

import io.github.nichetoolkit.socket.model.Jt808CanDataItem;

/**
 * <p>CanDataItemProcessor</p>
 * @author Cyan (snow22314@outlook.com)
 * @version v.1.0
 */
public interface Jt808CanDataItemProcessor {
    /**
     * 处理 CAN 总线数据项
     * @param canDataItem can总线数据项
     */
    void process(Jt808CanDataItem canDataItem);
}
