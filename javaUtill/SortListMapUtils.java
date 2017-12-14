package com.ailk.job.yuyueapi;
import com.ailk.utils.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by yumg on 2016/8/18.
 */
public class SortListMapUtils {


    /**
     * 快速排序法：对由Map组成的List按照Map的SortMapKey进行部分排序
     *
     * @param ls
     * @param strSortMapKey 排序所依据的Map的key
     */

    public  void sortList4Map(List<Map> ls, String strSortMapKey, String sortType) {
        if (ls != null && ls.size() > 0) {
            if ("ASC".equalsIgnoreCase(sortType)) {
                insertSortAsc(ls, strSortMapKey);
            } else if ("DESC".equalsIgnoreCase(sortType)) {
                insertSortDesc(ls, strSortMapKey);
            }
        }

    }

    public void insertSortAsc(List<Map> numInfoList, String strSortMapKey) {
        Map key = null;
        int j;
        for (int i = 1; i < numInfoList.size(); i++) {
            j = i - 1;
            key = numInfoList.get(i);
            Long strSort = StringUtils.toLong(key.get(strSortMapKey));
            while (j >= 0 && strSort < StringUtils.toLong(numInfoList.get(j).get(
                    strSortMapKey))) {
                numInfoList.set(j + 1, numInfoList.get(j));
                j--;
            }
            numInfoList.set(j + 1, key);
        }
    }

    public void insertSortDesc(List<Map> numInfoList, String strSortMapKey) {
        Map key = null;
        int j;
        for (int i = 1; i < numInfoList.size(); i++) {
            j = i - 1;
            key = numInfoList.get(i);
            Long strSortKey =  StringUtils.toLong(key.get(strSortMapKey));
            while (j >= 0 && strSortKey > StringUtils.toLong(numInfoList.get(j).get(
                    strSortMapKey))) {
                numInfoList.set(j + 1, numInfoList.get(j));
                j--;
            }
            numInfoList.set(j + 1, key);
        }
    }

}
