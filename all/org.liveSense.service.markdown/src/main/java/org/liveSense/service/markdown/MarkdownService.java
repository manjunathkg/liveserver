package org.liveSense.service.markdown;

import org.pegdown.PegDownProcessor;
import org.pegdown.Parser;
import org.pegdown.LinkRenderer;

public interface MarkdownService {
	public PegDownProcessor getDefaultProcessor();
    public PegDownProcessor getProcessor();
    public PegDownProcessor getProcessor(int options);
    public PegDownProcessor getProcessor(Parser parser);
    public String markdownToHtml(String markdownSource);
    public String markdownToHtml(String markdownSource, LinkRenderer linkRenderer);
    public String markdownToHtml(char[] markdownSource);
    public String markdownToHtml(char[] markdownSource, LinkRenderer linkRenderer);
}
