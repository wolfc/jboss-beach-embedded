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
package org.jboss.beach.embedded;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ParentLastURLClassLoader extends URLClassLoader
{
   public ParentLastURLClassLoader(URL[] urls)
   {
      super(urls);
   }

   public ParentLastURLClassLoader(URL[] urls, ClassLoader parent)
   {
      super(urls, parent);
   }

   @Override
   protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
   {
      // First, check if the class has already been loaded
      Class c = findLoadedClass(name);
      if (c == null)
      {
         try
         {
            c = findClass(name);
         }
         catch(ClassNotFoundException e)
         {
            // TODO: this might findClass twice
            c = super.loadClass(name, resolve);
         }
      }
      if (resolve)
      {
         resolveClass(c);
      }
      return c;
   }
}
