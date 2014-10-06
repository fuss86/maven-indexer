package org.apache.maven.index;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0    
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.maven.index.context.IndexingContext;

public abstract class AbstractNexusIndexerTest
    extends AbstractIndexCreatorHelper
{
    protected Indexer indexer;

    protected Directory indexDir = new RAMDirectory();

    protected IndexingContext context;

    @Override
    protected void setUp()
        throws Exception
    {
//        indexDir = new SimpleFSDirectory(new File("/tmp/nexus-test"));
        super.setUp();
        // FileUtils.deleteDirectory( indexDir );
        indexer = lookup( Indexer.class );
        prepareIndexer(indexer);
    }

    @Override
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
        // TODO: Brian reported, does not work on Windows because of left open files?
        // FileUtils.deleteDirectory( indexDir );
    }

    protected abstract void prepareIndexer(Indexer indexer)
        throws Exception;

    protected void assertGroup( int expected, String group, IndexingContext context )
        throws IOException
    {
        // ArtifactInfo.UINFO - UN_TOKENIZED
        // ArtifactInfo.GROUP_ID - TOKENIZED

        Term term = new Term( ArtifactInfo.GROUP_ID, group );
        PrefixQuery pq = new PrefixQuery( term );

        // new WildcardQuery( //
        // SpanTermQuery pq = new SpanTermQuery( term );
        // PhraseQuery pq = new PhraseQuery();
        // pq.add( new Term( ArtifactInfo.UINFO, group + "*" ) );

        FlatSearchResponse response = indexer.searchFlat( new FlatSearchRequest( pq, context ) );
        Collection<ArtifactInfo> artifacts = response.getResults();
        assertEquals( artifacts.toString(), expected, artifacts.size() );
    }

}
