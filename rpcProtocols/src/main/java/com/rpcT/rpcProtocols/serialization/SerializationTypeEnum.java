package com.rpcT.rpcProtocols.serialization;

import lombok.Getter;

public enum SerializationTypeEnum {
    // 0x10 实际就是 type，我们可以用 SEnum.JSON 来和 enum 中的某个枚举类型比较，SEnum.JSON.getType() 获得对应的 type
    // HESSIAN 和 JSON 每种都是一个 SerializationTypeEnum 类型
    HESSIAN(0x10),
    JSON(0x20);

    @Getter
    private final int type; // 设置后即不可再更改

    // 这个 type 用 byte 更好
    SerializationTypeEnum(int type) {
        this.type = type;
    }

    // 我们设置序列化类型时传入 0x10 或者 0x20 来选择哪种类型
    public static SerializationTypeEnum findByType(byte serializationType) {
        for (SerializationTypeEnum typeEnum : SerializationTypeEnum.values()) {
            if (typeEnum.getType() == serializationType) {
                return typeEnum;
            }
        }
        return HESSIAN;
    }

}
