package com.aaden.pay.service.resouce.lianlian;

/**
 *  @Description 连连工具类
 *  @author aaden
 *  @date 2017年12月2日
 */
public enum LianlianSignTypeEnum
{

    RSA("RSA", "RSA签名"),
    MD5("MD5", "MD5签名");

    private final String code;
    private final String msg;

    LianlianSignTypeEnum(String code, String msg)
    {
        this.code = code;
        this.msg = msg;
    }

    public String getCode()
    {
        return code;
    }

    public String getMsg()
    {
        return msg;
    }
    public static boolean isSignType(String code)
    {
        for (LianlianSignTypeEnum s : LianlianSignTypeEnum.values())
        {
            if (s.getCode().equals(code))
            {
                return true;
            }
        }
        return false;
    }
}
