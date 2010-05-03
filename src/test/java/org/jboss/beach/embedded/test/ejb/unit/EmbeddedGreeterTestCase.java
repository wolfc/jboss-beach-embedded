/*
 * JBoss, Home of Professional Open Source
 * Copyright (c) 2010, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.beach.embedded.test.ejb.unit;

import org.jboss.alunite.classloading.AluniteClassLoader;
import org.jboss.beach.embedded.EmbeddedRunner;
import org.jboss.beach.embedded.test.ejb.GreeterLocal;
import org.jboss.embedded.api.server.JBossASEmbeddedServer;
import org.jboss.embedded.api.server.JBossASEmbeddedServerFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.InitialContext;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
@RunWith(EmbeddedRunner.class)
public class EmbeddedGreeterTestCase
{
   private static JBossASEmbeddedServer server;
   
   @BeforeClass
   public static void beforeClass() throws Exception
   {
      String jbossHome = System.getenv("JBOSS_HOME");
      if(jbossHome == null)
         jbossHome = System.getProperty("jboss.home");
      if(jbossHome == null)
         throw new IllegalStateException("Neither JBOSS_HOME (env) nor jboss.home (property) is set");

      /*
      // TODO: remove this when JBAS-6744 is fixed
      String useUnorderedSequence = System.getProperty("xb.builder.useUnorderedSequence");
      if (useUnorderedSequence == null)
         System.setProperty("xb.builder.useUnorderedSequence", "true");

      // Workaround for http://community.jboss.org/message/532042#532042 // TODO -- remove this after XB update/resolution
      System.setProperty("xb.builder.repeatableParticleHandlers", "false");
      */

      // Initialize the JDK logmanager
      String logManagerClassName = System.getProperty("java.util.logging.manager");
      if (logManagerClassName == null)
      {
         System.setProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager");
         String pluginClass = System.getProperty("org.jboss.logging.Logger.pluginClass");
         if (pluginClass == null)
         {
            System.setProperty("org.jboss.logging.Logger.pluginClass", "org.jboss.logging.logmanager.LoggerPluginImpl");
            // disable log4j configuration, in case any early modules trigger it
            System.setProperty("log4j.defaultInitOverride", "true");
         }
      }

      File jbossHomeDir = new File(jbossHome);
      URL jbossHomeURL = jbossHomeDir.toURI().toURL();
      System.out.println(jbossHomeURL);
      URL urls[] = {
              new URL(jbossHomeURL, "lib/jboss-logmanager.jar"),
              new URL(jbossHomeURL, "lib/jboss-logging.jar"),
      };
      URLClassLoader bootCl = new URLClassLoader(urls, null);
      URL embeddedURLs[] = {
              // logging
              new URL(jbossHomeURL, "lib/jboss-logmanager.jar"),
              new URL(jbossHomeURL, "lib/jboss-logging.jar"),

              new URL(jbossHomeURL, "common/lib/jboss-embedded-core.jar"),

              // bootstrap
              new URL(jbossHomeURL, "lib/jboss-bootstrap-impl-as.jar"),
              new URL(jbossHomeURL, "lib/jboss-bootstrap-spi-as.jar"),
              new URL(jbossHomeURL, "lib/jboss-bootstrap-spi-mc.jar"),
              new URL(jbossHomeURL, "lib/jboss-bootstrap-api-mc.jar"),
              new URL(jbossHomeURL, "lib/jboss-bootstrap-spi.jar"),
              new URL(jbossHomeURL, "lib/jboss-bootstrap-impl-mc.jar"),
              new URL(jbossHomeURL, "lib/jboss-bootstrap-impl-base.jar"),
              new URL(jbossHomeURL, "lib/jboss-bootstrap-api.jar"),

              // to start MC
              new URL(jbossHomeURL, "lib/jboss-kernel.jar"),
              new URL(jbossHomeURL, "lib/jboss-reflect.jar"),
              new URL(jbossHomeURL, "lib/jboss-dependency.jar"),
              new URL(jbossHomeURL, "lib/jboss-mdr.jar"),

              // jboss-cl
              new URL(jbossHomeURL, "lib/jboss-classloader.jar"),
              new URL(jbossHomeURL, "lib/jboss-classloading.jar"),
              new URL(jbossHomeURL, "lib/jboss-classloading-spi.jar"),
              new URL(jbossHomeURL, "lib/jboss-classloading-vfs.jar"),

              // aop
              new URL(jbossHomeURL, "lib/javassist.jar"),
              new URL(jbossHomeURL, "lib/jboss-aop.jar"),
              new URL(jbossHomeURL, "lib/jboss-aop-mc-int.jar"),
              new URL(jbossHomeURL, "lib/trove.jar"),

              // vfs
              new URL(jbossHomeURL, "lib/jboss-vfs.jar"),

              // huh
              new URL(jbossHomeURL, "lib/javax.inject.jar"),

              // managed
              new URL(jbossHomeURL, "lib/jboss-managed.jar"),

              // classpool
              new URL(jbossHomeURL, "lib/jboss-classpool.jar"),
              new URL(jbossHomeURL, "lib/jboss-classpool-scoped.jar"),

              // metatype
              new URL(jbossHomeURL, "lib/jboss-metatype.jar"),

              new URL(jbossHomeURL, "lib/jbossxb.jar"),
              new URL(jbossHomeURL, "lib/jboss-common-core.jar"),

              // see embedded-core pom
              new URL(jbossHomeURL, "lib/jboss-deployers-core-spi.jar"),
              new URL(jbossHomeURL, "lib/jboss-deployers-client-spi.jar"),

              // meuk
              new URL(jbossHomeURL, "lib/resolver.jar"),

              // why, why? (jbossweb)
              new URL(jbossHomeURL, "lib/concurrent.jar"),
      };
      URLClassLoader embeddedCl = new URLClassLoader(embeddedURLs);
      //AluniteClassLoader cl = new AluniteClassLoader(embeddedCl, ClassLoader.getSystemClassLoader());
      AluniteClassLoader cl = new AluniteClassLoader(ClassLoader.getSystemClassLoader(), embeddedCl);

      Thread.currentThread().setContextClassLoader(cl);

      server = JBossASEmbeddedServerFactory.createServer(cl);
      server.getConfiguration().serverName("default");
      server.initialize();
      server.start();
   }
   
   @Test
   public void testGreeter() throws Exception
   {
      InitialContext ctx = new InitialContext();
      GreeterLocal bean = (GreeterLocal) ctx.lookup("GreeterBean/local");
   }
}
