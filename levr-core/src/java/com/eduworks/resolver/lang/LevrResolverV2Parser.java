package com.eduworks.resolver.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.eduworks.resolver.lang.output.resolverv2Lexer;
import com.eduworks.resolver.lang.output.resolverv2Parser;
import com.eduworks.util.Tuple;

public class LevrResolverV2Parser
{
	public static Logger	log	= Logger.getLogger(LevrResolverParser.class);

	public static Tuple<Map<String, JSONObject>,Map<String, JSONObject>> decodeStreams(File fileToDecode) throws JSONException
	{
		FileInputStream input = null;
		try
		{
			input = new FileInputStream(fileToDecode);
			resolverv2Lexer rl = new resolverv2Lexer(new ANTLRStringStream(IOUtils.toString(input)));
			CommonTokenStream tokens = new CommonTokenStream(rl);
			resolverv2Parser rp = new resolverv2Parser(tokens);
			try
			{
				rp.parse();
			}
			catch (RecognitionException e)
			{
				e.printStackTrace();
			}
			log.info("Reloaded Resolver V2 Code File. " + rp.servlets.size() + " servlets detected.");
			log.debug(rp.servlets.keySet());
			System.out.println(rp.servlets.keySet());
			if (rp.functions.size() > 0)
				System.out.println(rp.functions.keySet());
				
			return new Tuple<Map<String, JSONObject>,Map<String, JSONObject>>(rp.servlets,rp.functions);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			IOUtils.closeQuietly(input);
		}
	}
}
