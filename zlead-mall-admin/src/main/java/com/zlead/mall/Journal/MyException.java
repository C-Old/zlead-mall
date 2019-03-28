/**
 * @program: zlead-mall
 * @description:异常类
 * @author: ytchen
 * @create: 2019-03-28 17:13
 **/
package com.zlead.mall.Journal;

/**
 * 自定义异常
 * @author ytchen
 * @date 2019年3月28日
 */
public class MyException extends Exception {

    private static final long serialVersionUID = 1L;

    private String code;
    private String msg;

    public MyException() {}

    public MyException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}

