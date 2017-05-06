Text Justification

---

TextView 一种分散对齐实现方式。

使用方式：

```java
TextJustification.justify(textView);
```

原理：

测量 TextView 原始每行的空白宽度总和，使用 Spannable 平均到每个空格上。

效果如下：

![Preview](art/preview.jpg)


