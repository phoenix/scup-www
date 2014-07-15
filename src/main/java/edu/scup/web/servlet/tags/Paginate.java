package edu.scup.web.servlet.tags;

import org.springframework.data.domain.Page;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class Paginate extends TagSupport {
    private static final long serialVersionUID = -3773780445136334061L;
    private Page page;

    @Override
    public int doEndTag() throws JspException {
        String contextPath = pageContext.getServletContext().getContextPath();

        StringBuilder sb = new StringBuilder();
        sb.append("<table skymobiEscapeHtml=\"false\" width=\"100%\" border=0 cellspacing=0 cellpadding=0>");
        sb.append("<tr>");
        sb.append("<td align='left'><div align=\"left\"><span class=\"pageStyle\">&nbsp;&nbsp;&nbsp;&nbsp;共有<strong> ");
        sb.append(page.getTotalElements());
        sb.append("</strong> 条记录，当前第<input onkeydown=\"if(event.keyCode==13)jumpToZeroBasedPage(this)\" onKeyup=\"value=value.replace(/[^\\d]/g,'')\" type='text' id='toPageNum' style='width: 25px;' value=' ");
        sb.append(page.getNumber() + 1);
        sb.append("'/>  页，共 <strong id='totalPage'>");
        sb.append(page.getTotalPages());
        sb.append("</strong> 页</span>");
        sb.append("</div></td>");

        sb.append("<td align='right'><table width=\"200\" border=\"0\" align=\"right\" cellpadding=\"0\" cellspacing=\"0\">");
        sb.append("<tr>");

        if (page.getTotalPages() == 1 || page.getNumber() == 0) {
            sb.append("<td width=\"42\"><div align=\"center\"><img src=\"").append(contextPath)
                    .append("/images/first_dis.gif\" width=\"40\" height=\"15\"/></div></td>");
        } else {
            sb.append("<td width=\"42\"><div align=\"center\"><img src=\"").append(contextPath)
                    .append("/images/first.gif\" width=\"40\" height=\"15\" onclick=\"javascript:jumpPage(0)\" style=\"cursor:hand\"/></div></td>");
        }

        if (page.hasPreviousPage()) {
            sb.append("<td width=\"42\"><div align=\"center\"><img src=\"").append(contextPath).append("/images/pre.gif\" width=\"45\" height=\"15\" onclick=\"javascript:jumpPage(")
                    .append(page.getNumber() - 1).append(")\" style=\"cursor:hand\" /></div></td>");
        } else {
            sb.append("<td width=\"42\"><div align=\"center\"><img src=\"").append(contextPath).append("/images/pre_dis.gif\" width=\"45\" height=\"15\"  /></div></td>");
        }
        if (page.hasNextPage()) {
            sb.append("<td width=\"42\"><div align=\"center\"><img src=\"").append(contextPath).append("/images/next.gif\" width=\"45\" height=\"15\" onclick=\"javascript:jumpPage(")
                    .append(page.getNumber() + 1).append(")\" style=\"cursor:hand\"/></div></td>");
        } else {
            sb.append("<td width=\"42\"><div align=\"center\"><img src=\"").append(contextPath).append("/images/next_dis.gif\" width=\"45\" height=\"15\" /></div></td>");
        }

        if (page.getTotalPages() <= 1 || page.getTotalPages() == page.getNumber()+1) {
            sb.append("<td width=\"42\"><div align=\"center\"><img src=\"").append(contextPath).append("/images/last_dis.gif\" width=\"40\" height=\"15\" /></div></td>");
        } else {
            sb.append("<td width=\"42\"><div align=\"center\"><img src=\"").append(contextPath).append("/images/last.gif\" width=\"40\" height=\"15\" onclick=\"javascript:jumpPage(")
                    .append(page.getTotalPages()-1).append(")\" style=\"cursor:hand\"/></div></td>");
        }

        sb.append("</tr>");
        sb.append("</table></td>");
        sb.append("</tr>");
        sb.append("</table>");

        try {
            pageContext.getOut().print(sb.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }
        return Tag.EVAL_PAGE;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
