package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.*;
import net.minecraft.LogManager;
import net.minecraft.Logger;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserList<K, V extends UserListEntry<K>>
{
    protected static final Logger logger = LogManager.getLogger();
    protected final Gson gson;
    private final File saveFile;
    private final Map<String, V> values = Maps.newHashMap();
    private boolean lanServer = true;
    private static final ParameterizedType saveFileFormat = new ParameterizedType()
    {
        public Type[] getActualTypeArguments()
        {
            return new Type[] {UserListEntry.class};
        }
        public Type getRawType()
        {
            return List.class;
        }
        public Type getOwnerType()
        {
            return null;
        }
    };

    public UserList(File saveFile)
    {
        this.saveFile = saveFile;
        GsonBuilder gsonbuilder = new GsonBuilder().setPrettyPrinting();
        gsonbuilder.registerTypeHierarchyAdapter(UserListEntry.class, new UserList.Serializer());
        this.gson = gsonbuilder.create();
    }

    public boolean isLanServer()
    {
        return this.lanServer;
    }

    public void setLanServer(boolean state)
    {
        this.lanServer = state;
    }

    public File getSaveFile()
    {
        return this.saveFile;
    }

    /**
     * Adds an entry to the list
     */
    public void addEntry(V entry)
    {
        this.values.put(this.getObjectKey(entry.getValue()), entry);

        try
        {
            this.writeChanges();
        }
        catch (IOException ioexception)
        {
            logger.warn("Could not save the list after adding a user.", ioexception);
        }
    }

    public V getEntry(K obj)
    {
        this.removeExpired();
        return this.values.get(this.getObjectKey(obj));
    }

    public void removeEntry(K p_152684_1_)
    {
        this.values.remove(this.getObjectKey(p_152684_1_));

        try
        {
            this.writeChanges();
        }
        catch (IOException ioexception)
        {
            logger.warn("Could not save the list after removing a user.", ioexception);
        }
    }

    public String[] getKeys()
    {
        return this.values.keySet().toArray(new String[0]);
    }

    public boolean isEmpty()
    {
        return this.values.size() < 1;
    }

    /**
     * Gets the key value for the given object
     */
    protected String getObjectKey(K obj)
    {
        return obj.toString();
    }

    protected boolean hasEntry(K entry)
    {
        return this.values.containsKey(this.getObjectKey(entry));
    }

    /**
     * Removes expired bans from the list. See {@link BanEntry#hasBanExpired}
     */
    private void removeExpired()
    {
        List<K> list = Lists.newArrayList();

        for (V v : this.values.values())
        {
            if (v.hasBanExpired())
            {
                list.add(v.getValue());
            }
        }

        for (K k : list)
        {
            this.values.remove(k);
        }
    }

    protected UserListEntry<K> createEntry(JsonObject entryData)
    {
        return new UserListEntry(null, entryData);
    }

    protected Map<String, V> getValues()
    {
        return this.values;
    }

    public void writeChanges() throws IOException
    {
        Collection<V> collection = this.values.values();
        String s = this.gson.toJson(collection);
        BufferedWriter bufferedwriter = null;

        try
        {
            bufferedwriter = Files.newWriter(this.saveFile, Charsets.UTF_8);
            bufferedwriter.write(s);
        }
        finally
        {
            IOUtils.closeQuietly(bufferedwriter);
        }
    }

    public void readSavedFile() throws IOException, FileNotFoundException
    {
        Collection<UserListEntry<K>> collection = null;
        BufferedReader bufferedreader = null;

        try
        {
            bufferedreader = Files.newReader(this.saveFile, Charsets.UTF_8);
            collection = this.gson.fromJson(bufferedreader, saveFileFormat);
        }
        finally
        {
            IOUtils.closeQuietly(bufferedreader);
        }

        if (collection != null)
        {
            this.values.clear();

            for (UserListEntry<K> userlistentry : collection)
            {
                if (userlistentry.getValue() != null)
                {
                    this.values.put(this.getObjectKey(userlistentry.getValue()), (V)userlistentry);
                }
            }
        }
    }

    class Serializer implements JsonDeserializer<UserListEntry<K>>, JsonSerializer<UserListEntry<K>>
    {
        private Serializer()
        {
        }

        public JsonElement serialize(UserListEntry<K> p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            JsonObject jsonobject = new JsonObject();
            p_serialize_1_.onSerialization(jsonobject);
            return jsonobject;
        }

        public UserListEntry<K> deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            if (p_deserialize_1_.isJsonObject())
            {
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
				return UserList.this.createEntry(jsonobject);
            }
			return null;
		}
    }
}
