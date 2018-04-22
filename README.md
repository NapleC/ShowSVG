实现思路 ：
1、利用 DOM 解析 SVG 的代码，将属性 pathData 等数据解析；
2、利用系统提供的方法 canvas.drawPath(Path,Paint) 将 path 区域绘制在画布上。

用 http://inloop.github.io/svg2android/ 网站将 SVG 资源转换成相应的 Android 代码
写了一个路径解析兼容类（兼容标准svg）(com.hl.batik.utils.PathParser)，
再做标准解析（com.hl.batik.NormalSVG.NormalSVG）可正常显示。
