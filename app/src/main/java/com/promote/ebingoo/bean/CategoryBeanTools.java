package com.promote.ebingoo.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/5.
 */
public class CategoryBeanTools {
    private ArrayList<CategoryBeen> response;

    public ArrayList<CategoryBeen> getResponse() {
        return response;
    }

    public void setResponse(ArrayList<CategoryBeen> response) {
        this.response = response;
    }


    public static ArrayList<CategoryBeen> getCategories(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<CategoryBeanTools>() {
        }.getType();


        ArrayList<CategoryBeen> categoryBeens = ((CategoryBeanTools) gson.fromJson(json, type)).getResponse();


        return divideSubCategory(categoryBeens);
    }

    /**
     * 将子分类添加到父分类中。
     *
     * @param categoryBeens
     * @return
     */
    private static ArrayList<CategoryBeen> divideSubCategory(ArrayList<CategoryBeen> categoryBeens) {

        ArrayList<CategoryBeen> categoryBeens_copy = new ArrayList<CategoryBeen>(categoryBeens);

        int parentId;
        for (CategoryBeen category : categoryBeens) {
            parentId = category.getParent_id();
            if (parentId != 0) {    //子分类。
                categoryBeens_copy.remove(category);
                SubCategoryBean subCategoryBean = new SubCategoryBean();
                subCategoryBean.setName(category.getName());
                subCategoryBean.setParent_id(category.getParent_id());
                subCategoryBean.setId(category.getId());
                getCategoryByParentId(parentId, categoryBeens_copy).getSubCategorys().add(subCategoryBean);
            }

        }

        return categoryBeens_copy;

    }

    private static CategoryBeen getCategoryByParentId(int id, ArrayList<CategoryBeen> categoryBeens) {

        for (CategoryBeen categoryBeen : categoryBeens) {

            if (categoryBeen.getId() == id) {
                return categoryBeen;
            }

        }

        return null;
    }


}
