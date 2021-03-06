/*
 * Copyright 2016 Axel Faust
 *
 * Licensed under the Eclipse Public License (EPL), Version 1.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.axelfaust.alfresco.enhScriptEnv.common.script.converter.general;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.alfresco.util.PropertyCheck;
import org.mozilla.javascript.NativeObject;
import org.springframework.beans.factory.InitializingBean;

import de.axelfaust.alfresco.enhScriptEnv.common.script.converter.ValueConverter;
import de.axelfaust.alfresco.enhScriptEnv.common.script.converter.ValueInstanceConverterRegistry;
import de.axelfaust.alfresco.enhScriptEnv.common.script.converter.ValueInstanceConverterRegistry.ValueInstanceConverter;

/**
 * A simple convert to handle Map-to-Map conversion where the original Map instance is retained but keys and values are recursively put
 * through conversions.
 *
 * @author Axel Faust
 */
public class MapConverter implements ValueInstanceConverter, InitializingBean
{

    protected ValueInstanceConverterRegistry registry;

    /**
     * @param registry
     *            the registry to set
     */
    public void setRegistry(final ValueInstanceConverterRegistry registry)
    {
        this.registry = registry;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet()
    {
        PropertyCheck.mandatory(this, "registry", this.registry);

        this.registry.registerValueInstanceConverter(Map.class, this);
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public int getForJavaConversionConfidence(final Class<?> valueInstanceClass, final Class<?> expectedClass)
    {
        final int confidence;
        // in Rhino 1.7, NativeObject implements Map which makes things difficult
        if (Map.class.isAssignableFrom(valueInstanceClass) && !NativeObject.class.isAssignableFrom(valueInstanceClass)
                && (expectedClass.isAssignableFrom(Map.class) || expectedClass.equals(valueInstanceClass)))
        {
            confidence = LOW_CONFIDENCE;
        }
        else
        {
            confidence = LOWEST_CONFIDENCE;
        }
        return confidence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canConvertValueForJava(final Object value, final ValueConverter globalDelegate, final Class<?> expectedClass)
    {
        // in Rhino 1.7, NativeObject implements Map which makes things difficult
        boolean canConvert = value instanceof Map<?, ?> && !(value instanceof NativeObject)
                && (expectedClass.isAssignableFrom(Map.class) || expectedClass.equals(value.getClass()));

        final Map<?, ?> map = (Map<?, ?>) value;

        for (final Object key : map.keySet())
        {
            final Object valueForKey = map.get(key);

            canConvert = canConvert && globalDelegate.canConvertValueForJava(key) && globalDelegate.canConvertValueForJava(valueForKey);
        }

        return canConvert;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object convertValueForJava(final Object value, final ValueConverter globalDelegate, final Class<?> expectedClass)
    {
        // in Rhino 1.7, NativeObject implements Map which makes things difficult
        if (!(value instanceof Map<?, ?>) || value instanceof NativeObject)
        {
            throw new IllegalArgumentException("value must be a Map");
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        final Map<Object, Object> map = (Map) value;

        final Collection<Object> keys = new HashSet<Object>(map.keySet());
        for (final Object key : keys)
        {
            final Object valueForKey = map.get(key);

            final Object convertedKey = globalDelegate.convertValueForJava(key);
            final Object convertedValue = globalDelegate.convertValueForJava(valueForKey);

            if (key != convertedKey || valueForKey != convertedValue)
            {
                map.remove(key);
                map.put(convertedKey, convertedValue);
            }

        }

        return map;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public int getForScriptConversionConfidence(final Class<?> valueInstanceClass, final Class<?> expectedClass)
    {
        final int confidence;
        if (Map.class.isAssignableFrom(valueInstanceClass)
                && (expectedClass.isAssignableFrom(Map.class) || expectedClass.equals(valueInstanceClass)))
        {
            confidence = LOW_CONFIDENCE;
        }
        else
        {
            confidence = LOWEST_CONFIDENCE;
        }
        return confidence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canConvertValueForScript(final Object value, final ValueConverter globalDelegate, final Class<?> expectedClass)
    {
        boolean canConvert = value instanceof Map<?, ?>
                && (expectedClass.isAssignableFrom(Map.class) || expectedClass.equals(value.getClass()));

        final Map<?, ?> map = (Map<?, ?>) value;

        for (final Object key : map.keySet())
        {
            final Object valueForKey = map.get(key);

            canConvert = canConvert && globalDelegate.canConvertValueForScript(key) && globalDelegate.canConvertValueForScript(valueForKey);
        }

        return canConvert;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object convertValueForScript(final Object value, final ValueConverter globalDelegate, final Class<?> expectedClass)
    {
        if (!(value instanceof Map<?, ?>))
        {
            throw new IllegalArgumentException("value must be a Map");
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        final Map<Object, Object> map = (Map) value;

        final Collection<Object> keys = new HashSet<Object>(map.keySet());
        for (final Object key : keys)
        {
            final Object valueForKey = map.get(key);

            final Object convertedKey = globalDelegate.convertValueForScript(key);
            final Object convertedValue = globalDelegate.convertValueForScript(valueForKey);

            if (key != convertedKey || valueForKey != convertedValue)
            {
                map.remove(key);
                map.put(convertedKey, convertedValue);
            }

        }

        return map;
    }
}
