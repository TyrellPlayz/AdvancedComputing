package com.tyrellplayz.tech_craft.api.content;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.tech_craft.api.component.ItemList;
import com.tyrellplayz.tech_craft.api.icon.Icon;
import com.tyrellplayz.zlib.util.ClickListener;

import java.util.*;

public class RightClickMenu extends LayeredContent {

    private final Map<String,Category> categories;

    private Layer mainLayer;

    public RightClickMenu(Map<String, Category> categories) {
        this.categories = categories;
        this.setBorder(false);
        this.setMovable(false);
        this.setShowIcon(false);
    }

    @Override
    public void onLoad() {
        mainLayer = new Layer(this,50,50);

        ItemList<RightClickMenu.Item> list = new ItemList<>(0,0,mainLayer.getWidth(),mainLayer.getHeight());
        list.setGetName(Item::getTitle);
        list.setGetIcon(Item::getIcon);
        list.setOnItemSelected((item, index) -> {
            item.getClickListener().onClick(0);
            this.getWindow().close();
            return true;
        });

        list.setItems(categories.get(Builder.DEFAULT_CATEGORY).items);

        mainLayer.addComponent(list);

        setActiveLayer(mainLayer);
    }

    @Override
    public void onFocusChanged(boolean lostFocus) {
        super.onFocusChanged(lostFocus);
        if (this.getWindow().isOpen() && lostFocus) {
            this.getWindow().close();
        }
    }

    public static class Builder {

        public static String DEFAULT_CATEGORY = "default";

        private final int left, top;
        private final Map<String,Category> categories;

        public Builder(int left, int top) {
            this.left = left;
            this.top = top;
            categories = new HashMap<>();
            createCategory(DEFAULT_CATEGORY);
        }

        public Builder addItem(Item item) {
            addItem(DEFAULT_CATEGORY,item);
            return this;
        }

        public Builder addItems(Item... items) {
            addItems(DEFAULT_CATEGORY,items);
            return this;
        }

        public Builder addAllItems(Collection<Item> items) {
            addAllItems(DEFAULT_CATEGORY,items);
            return this;
        }

        public Builder addItem(String categoryKey, Item item) {
            categories.get(categoryKey).addItem(item);
            return this;
        }

        public Builder addItems(String categoryKey, Item... items) {
            categories.get(categoryKey).addAllItems(Arrays.stream(items).toList());
            return this;
        }

        public Builder addAllItems(String categoryKey, Collection<Item> items) {
            categories.get(categoryKey).addAllItems(items);
            return this;
        }

        public Builder createCategory(String key) {
            categories.put(key,new Category(key));
            return this;
        }

        public Builder addCategory(Category category) {
            categories.put(category.getKey(),category);
            return this;
        }

        public RightClickMenu build() {
            return new RightClickMenu(categories);
        }

        public int getLeft() {
            return left;
        }

        public int getTop() {
            return top;
        }
    }

    public static class Category {

        private final String key;
        private final List<Item> items;

        public Category() {
            this("");
        }

        public Category(String key) {
            this.key = key;
            this.items = new ArrayList<>();
        }

        public List<Item> getItems() {
            return this.items;
        }

        public void addItem(Item item) {
            this.items.add(item);
        }

        public void addItems(Item... items) {
            this.items.addAll(Arrays.stream(items).toList());
        }

        public void addAllItems(Collection<Item> items) {
            this.items.addAll(items);
        }

        public String getKey() {
            return key;
        }
    }

    public static class Item {

        private final String title;
        private final Icon icon;
        private final ClickListener clickListener;

        public Item(String title, ClickListener clickListener) {
            this(title,null,clickListener);
        }

        public Item(String title, Icon icon, ClickListener clickListener) {
            this.title = title;
            this.icon = icon;
            this.clickListener = clickListener;
        }

        public String getTitle() {
            return title;
        }

        public Icon getIcon() {
            return icon;
        }

        public ClickListener getClickListener() {
            return clickListener;
        }
    }

}
