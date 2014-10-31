package com.promote.ebingoo.search;

/**
 * Created by ACER on 2014/9/2.
 *
 * 搜索list顯示的類型.
 *
 */
public enum SearchType {

    HISTORY(1), DEMAND(2), SUPPLY(3), INTERPRISE(4){

        public boolean isRest(){
            return true;
        }
    };

    private int value;

    private SearchType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }


}
