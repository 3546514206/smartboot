package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.w3c.dom.Element;

/**
 * @author qinluo
 * @date 2023/3/12 23:57
 * @since 1.0.8
 */
public class ScriptLoaderParser extends AbstractElementParser {

    @Override
    public String getElementName() {
        return ParseConstants.SCRIPT_LOADER;
    }

    @Override
    public void doParse(Element element, ParserContext context) {
        String locations = element.getAttribute("locations");
        AssertUtil.notBlank(locations, "script-loader locations must not be null!");

        ScriptLoader loader = new ScriptLoader();
        loader.locations(AuxiliaryUtils.splitByComma(locations).toArray(new String[0]));

        loader.exclude(AuxiliaryUtils.splitByComma(element.getAttribute("exclude")).toArray(new String[0]));
        loader.accept(AuxiliaryUtils.splitByComma(element.getAttribute("accept")).toArray(new String[0]));
        context.addScriptLoad(loader);
    }
}
