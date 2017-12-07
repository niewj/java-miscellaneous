/**
 * Created by niewj on 2017/5/17.
 */

String.prototype.replaceAll = function (s1, s2) {
    return this.replace(new RegExp(s1, "gm"), s2);
}

/**
 * 把所有的[下划线格式]变量改为[驼峰变量]
 * @param content 源字符串
 * @returns {*}
 */
function replaceUnderline2Camel(content){
    var s = content.replaceAll("_a", "A")
        .replaceAll("_b", "B")
        .replaceAll("_c", "C")
        .replaceAll("_d", "D")
        .replaceAll("_e", "E")
        .replaceAll("_f", "F")
        .replaceAll("_g", "G")
        .replaceAll("_h", "H")
        .replaceAll("_i", "I")
        .replaceAll("_j", "J")
        .replaceAll("_k", "K")
        .replaceAll("_l", "L")
        .replaceAll("_m", "M")
        .replaceAll("_n", "N")
        .replaceAll("_o", "O")
        .replaceAll("_p", "P")
        .replaceAll("_q", "Q")
        .replaceAll("_r", "R")
        .replaceAll("_s", "S")
        .replaceAll("_t", "T")
        .replaceAll("_u", "U")
        .replaceAll("_v", "V")
        .replaceAll("_w", "W")
        .replaceAll("_x", "X")
        .replaceAll("_y", "Y")
        .replaceAll("_z", "Z");

    return s;
}

/**
 * 把所有的[驼峰变量]变量改为[下划线格式]
 * @param content
 * @returns {*}
 */
function replaceCamel2Underline(content){
    var s = content.replaceAll("A", "_a")
        .replaceAll("B", "_b")
        .replaceAll("C", "_c")
        .replaceAll("D", "_d")
        .replaceAll("E", "_e")
        .replaceAll("F", "_f")
        .replaceAll("G", "_g")
        .replaceAll("H", "_h")
        .replaceAll("I", "_i")
        .replaceAll("J", "_j")
        .replaceAll("K", "_k")
        .replaceAll("L", "_l")
        .replaceAll("M", "_m")
        .replaceAll("N", "_n")
        .replaceAll("O", "_o")
        .replaceAll("P", "_p")
        .replaceAll("Q", "_q")
        .replaceAll("R", "_r")
        .replaceAll("S", "_s")
        .replaceAll("T", "_t")
        .replaceAll("U", "_u")
        .replaceAll("V", "_v")
        .replaceAll("W", "_w")
        .replaceAll("X", "_x")
        .replaceAll("Y", "_y")
        .replaceAll("Z", "_z")
        .replaceAll(" _string ", " String ")
        .replaceAll(" _byte ", " Byte ")
        .replaceAll(" _short ", " Short ")
        .replaceAll(" _int ", " int ")
        .replaceAll(" _integer ", " Integer ")
        .replaceAll(" _long ", " Long ")
        .replaceAll(" _double ", " Double ")
        .replaceAll(" _float ", " Float ")
        .replaceAll(" _boolean ", " Boolean ");

    return s;
}
