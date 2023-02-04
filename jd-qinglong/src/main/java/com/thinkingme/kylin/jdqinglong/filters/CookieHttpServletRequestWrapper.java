package com.thinkingme.kylin.jdqinglong.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.SessionCookieConfig;
import javax.servlet.http.*;
import java.util.Collection;

/**
 * <p>
 * csrf 包装器 添加samesite属性
 * </p>
 *
 * @author: huige
 * @date: 2023/1/12 9:03
 */
@Slf4j
public class CookieHttpServletRequestWrapper  extends HttpServletRequestWrapper {


    private final String SAME_SITE_ATTRIBUTE_VALUES = "SameSite=Lax";

    private static final String _I_PHONE_IOS_12 = "iPhone OS 12_";
    private static final String _I_PAD_IOS_12 = "iPad; CPU OS 12_";
    private static final String _MAC_OS_10_14 = " OS X 10_14_";
    private static final String _VERSION = "Version/";
    private static final String _SAFARI = "Safari";
    private static final String _EMBED_SAFARI = "(KHTML, like Gecko)";
    private static final String _CHROME = "Chrome/";
    private static final String _CHROMIUM = "Chromium/";
    private static final String _UC_BROWSER = "UCBrowser/";
    private static final String _ANDROID = "Android";

    public CookieHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public HttpSession getSession() {
        HttpSession session = super.getSession();
        SessionCookieConfig sessionCookieConfig = session.getServletContext().getSessionCookieConfig();
        String comment = sessionCookieConfig.getComment();
        if(comment!=null){
            sessionCookieConfig.setComment(comment+";"+SAME_SITE_ATTRIBUTE_VALUES);

        }

        return session;
    }

    @Override
    public HttpSession getSession(boolean create) {
        HttpSession session = super.getSession(create);

        if (create) {
            SessionCookieConfig sessionCookieConfig = session.getServletContext().getSessionCookieConfig();
            String comment = sessionCookieConfig.getComment();
            if(comment!=null){
                sessionCookieConfig.setComment(comment+";"+SAME_SITE_ATTRIBUTE_VALUES);
            }
        }

        return session;
    }


    /**
     * checks SameSite=None;Secure incompatible Browsers
     * https://www.chromium.org/updates/same-site/incompatible-clients
     * @param request
     * @return
     */
    public boolean isSameSiteInCompatibleClient(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");
        if (StringUtils.isNotBlank(userAgent)) {
            boolean isIos12 = isIos12(userAgent), isMacOs1014 = isMacOs1014(userAgent), isChromeChromium51To66 = isChromeChromium51To66(userAgent), isUcBrowser = isUcBrowser(userAgent);
            //TODO : Added for testing purpose. remove before Prod release.
            log.debug("*********************************************************************************");
            log.debug("is iOS 12 = "+isIos12+", is MacOs 10.14 = "+isMacOs1014+", is Chrome 51-66 = "+isChromeChromium51To66+", is Android UC Browser = "+isUcBrowser);
            log.debug("*********************************************************************************");
            return isIos12 || isMacOs1014 || isChromeChromium51To66 || isUcBrowser;
        }
        return false;
    }

    private boolean isIos12(String userAgent) {
        return StringUtils.contains(userAgent, _I_PHONE_IOS_12) || StringUtils.contains(userAgent, _I_PAD_IOS_12);
    }

    private boolean isMacOs1014(String userAgent) {
        return StringUtils.contains(userAgent, _MAC_OS_10_14)
                && ((StringUtils.contains(userAgent, _VERSION) && StringUtils.contains(userAgent, _SAFARI))  //Safari on MacOS 10.14
                || StringUtils.contains(userAgent, _EMBED_SAFARI)); // Embedded browser on MacOS 10.14
    }

    private boolean isChromeChromium51To66(String userAgent) {
        boolean isChrome = StringUtils.contains(userAgent, _CHROME), isChromium = StringUtils.contains(userAgent, _CHROMIUM);
        if (isChrome || isChromium) {
            int version = isChrome ? Integer.valueOf(StringUtils.substringAfter(userAgent, _CHROME).substring(0, 2))
                    : Integer.valueOf(StringUtils.substringAfter(userAgent, _CHROMIUM).substring(0, 2));
            return ((version >= 51) && (version <= 66));    //Chrome or Chromium V51-66
        }
        return false;
    }

    private boolean isUcBrowser(String userAgent) {
        if (StringUtils.contains(userAgent, _UC_BROWSER) && StringUtils.contains(userAgent, _ANDROID)) {
            String[] version = StringUtils.splitByWholeSeparator(StringUtils.substringAfter(userAgent, _UC_BROWSER).substring(0, 7), ".");
            int major = Integer.valueOf(version[0]), minor = Integer.valueOf(version[1]), build = Integer.valueOf(version[2]);
            return ((major != 0) && ((major < 12) || (major == 12 && (minor < 13)) || (major == 12 && minor == 13 && (build < 2)))); //UC browser below v12.13.2 in android
        }
        return false;
    }
}
