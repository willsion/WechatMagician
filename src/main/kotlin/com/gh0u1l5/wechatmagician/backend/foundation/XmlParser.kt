package com.gh0u1l5.wechatmagician.backend.foundation

import com.gh0u1l5.wechatmagician.Global.STATUS_FLAG_XML_PARSER
import com.gh0u1l5.wechatmagician.backend.WechatPackage
import com.gh0u1l5.wechatmagician.backend.foundation.base.EventCenter
import com.gh0u1l5.wechatmagician.backend.interfaces.IXmlParserHook
import com.gh0u1l5.wechatmagician.backend.interfaces.IXmlParserHookRaw
import com.gh0u1l5.wechatmagician.util.PackageUtil.findAndHookMethod
import de.robv.android.xposed.XC_MethodHook

object XmlParser : EventCenter() {

    private val pkg = WechatPackage

    @Suppress("UNCHECKED_CAST")
    @JvmStatic fun hookEvents() {
        findAndHookMethod(pkg.XMLParserClass, pkg.XMLParseMethod, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                notify("beforeXmlParse", { plugin ->
                    if (plugin is IXmlParserHookRaw) {
                        plugin.beforeXmlParse(param)
                    }
                })
            }
            override fun afterHookedMethod(param: MethodHookParam) {
                notify("afterXmlParse", { plugin ->
                    if (plugin is IXmlParserHookRaw) {
                        plugin.afterXmlParse(param)
                    }
                })
                notify("onXmlParse", { plugin ->
                    if (plugin is IXmlParserHook) {
                        val root = param.args[1] as String
                        val xml = param.result as MutableMap<String, String>?
                        if (xml != null) {
                            plugin.onXmlParse(root, xml)
                        }
                    }
                })
            }
        })

        pkg.setStatus(STATUS_FLAG_XML_PARSER, true)
    }
}