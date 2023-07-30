package id.tog.oauth2.util;

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtils {

    public static String fromBase64(String imageData) {
        return ImageUtils.fromBase64(imageData, PropertiesUtils.CDN_PATH, PropertiesUtils.CDN_BASEURL);
    }

    public static String fromBase64(String imageData, String basePath, String baseUrl) {
        return ImageUtils.fromBase64(imageData, new Date().getTime() + "", basePath, baseUrl);
    }

    @SuppressWarnings("RegExpRedundantEscape")
    public static String fromBase64(String imageData, String filename, String basePath, String baseUrl) {
        if (null == filename || null == basePath || null == baseUrl) {
            return imageData;
        }

        Base64.Decoder base64 = Base64.getDecoder();

        Pattern pattern = Pattern.compile("^data\\:image\\/([^\\;]+)\\;base64,(.*)");
        Matcher matcher = pattern.matcher(imageData);

        String fileExtension = null;
        byte[] byteString = null;

        if (matcher.find()) {
            fileExtension = matcher.group(1);
            byteString = base64.decode(matcher.group(2));
        }

        if (null == byteString || null == fileExtension) {
            return imageData;
        }

        filename += "." + fileExtension;

        boolean ok = true;
        File dir = new File(basePath);
        File file = new File(dir.getAbsolutePath() + "/" + filename);
        try {
            if (!dir.exists() && !dir.mkdirs()) {
                ok = false;
            }
        } catch (RuntimeException e) {
            ok = false;
        }

        String output = imageData;

        if (ok) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(byteString);
                fos.close();
                if (baseUrl.endsWith("/")) {
                    baseUrl = baseUrl.replaceAll("/$", "");
                }
                output = baseUrl + "/" + filename;
            } catch (IOException e) {}
        }

        return output;
    }

    @SuppressWarnings("UnusedAssignment")
    public static void removeFileFormUrl(String url, String basePath) {
        if (null == url || null == basePath) {
            return;
        }
        String image = url.replaceAll(".*/([^/]+)$", "$1");
        boolean success = false;
        if (!StringUtils.isEmpty(image)) {
            File file = new File(basePath + "/" + image);
            try {
                success = file.exists() && file.delete();
            } catch (Exception e) {}
        }

        System.out.println(image);
    }

    public static void removeFileFormUrl(String url) {
        ImageUtils.removeFileFormUrl(url, PropertiesUtils.CDN_PATH);
    }
}
