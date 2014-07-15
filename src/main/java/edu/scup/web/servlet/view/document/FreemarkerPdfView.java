package edu.scup.web.servlet.view.document;

import com.itextpdf.text.pdf.BaseFont;
import freemarker.template.Configuration;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.web.servlet.view.AbstractView;
import org.springside.modules.utils.Encodes;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Map;

public class FreemarkerPdfView extends AbstractView {
    private Configuration configuration;
    private String ftlFile;

    /**
     * This constructor sets the appropriate content type "application/pdf".
     * Note that IE won't take much notice of this, but there's not a lot we
     * can do about this. Generated documents should have a ".pdf" extension.
     */
    public FreemarkerPdfView(Configuration configuration, String ftlFile) {
        setContentType("application/pdf");
        this.ftlFile = ftlFile;
        this.configuration = configuration;
    }


    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override
    protected final void renderMergedOutputModel(
            Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileName = (String) model.get("fileName");
        if (fileName != null) {
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName) + ".pdf");
        }

        // IE workaround: write into byte array first.
        ByteArrayOutputStream baos = createTemporaryOutputStream();

        StringWriter sw = new StringWriter();
        configuration.getTemplate(ftlFile).process(model, sw);
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont(getClass().getResource("/simsun.ttc").getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        ServletContext sc = request.getSession().getServletContext();
        String rootpath = sc.getRealPath("");
        //把路径中的反斜杠转成正斜杠
        rootpath = rootpath.replaceAll("\\\\", "/");
        if (rootpath.startsWith("/")) {//fix linux path
            rootpath = rootpath.substring(1, rootpath.length());
        }
        renderer.setDocumentFromString(StringEscapeUtils.unescapeHtml4(sw.toString()), "file:/" + rootpath + "/");
        renderer.layout();
        renderer.createPDF(baos);

        // Flush to HTTP response.
        writeToResponse(response, baos);
    }
}
