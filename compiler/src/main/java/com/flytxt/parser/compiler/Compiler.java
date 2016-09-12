package com.flytxt.parser.compiler;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.flytxt.parser.compiler.parser.Parser;


@SpringBootApplication
@EnableAutoConfiguration
public class Compiler {

	public static void main(String args[]){
		SpringApplication.run(Compiler.class, args);
	}
	public Object compileNLoad(String scriptName) throws Exception{
		ScriptReaderUnitTest reader = new ScriptReaderUnitTest();
		Parser p = new Parser();
		reader.read(scriptName, p);

		Path root = Paths.get("scripts");
		Files.createDirectories(root);
		String file = scriptName.substring(0, scriptName.length()-3);
		File sourceFile = new File(root.toString(), "com/flytxt/utils/parser/"+file+".java");
		sourceFile.getParentFile().mkdirs();
		System.out.println("loc: "+sourceFile.getAbsolutePath());
		Files.write(sourceFile.toPath(), p.createProcessClass().getBytes(StandardCharsets.UTF_8));

		// Compile source file.
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, sourceFile.getPath());

		// Load and instantiate compiled class.
		URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toUri().toURL() });
		Class<?> cls = Class.forName("com.flytxt.utils.parser."+file, true, classLoader); // Should print "hello".
		return cls.newInstance();
		
	}
}