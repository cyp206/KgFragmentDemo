package com.snow.yp.kgdemo.greendao.manager;

import com.snow.yp.kgdemo.MyApp;
import com.snow.yp.kgdemo.greendao.FoolBean;

import java.util.List;

import static com.snow.yp.kgdemo.greendao.manager.DaoManager.getInstance;

/**
 * Created by y on 2017/11/10.
 */

public class FoolDaoUtils {

    /**
     * 增
     *
     * @param foolBean
     * @return
     */
    public static boolean insertFool(FoolBean foolBean) {
        long result = getInstance(MyApp.getContext()).getDaoSeesion().insert(foolBean);
        return result > 0;
    }

    /**
     * 删
     *
     * @param foolBean
     */
    public static void dealFool(FoolBean foolBean) {
        getInstance(MyApp.getContext()).getDaoSeesion().delete(foolBean);
    }

    /**
     * 改
     *
     * @param foolBean
     */
    public static void update(FoolBean foolBean) {
        getInstance(MyApp.getContext()).getDaoSeesion().update(foolBean);
    }

    /**
     * 查
     *
     * @return
     */
    public static List<FoolBean> sqlAll() {
        return getInstance(MyApp.getContext()).getDaoSeesion().loadAll(FoolBean.class);
    }
}
