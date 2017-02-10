/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.gaffer.hbasestore.operation.handler;

import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterable;
import uk.gov.gchq.gaffer.data.IsEdgeValidator;
import uk.gov.gchq.gaffer.data.TransformIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.hbasestore.HBaseStore;
import uk.gov.gchq.gaffer.hbasestore.retriever.HBaseRetriever;
import uk.gov.gchq.gaffer.hbasestore.utils.HBaseStoreConstants;
import uk.gov.gchq.gaffer.operation.GetOperation.IncludeEdgeType;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentEntitySeeds;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.operation.handler.OperationHandler;
import uk.gov.gchq.gaffer.user.User;
import java.util.Collections;

public class GetAdjacentEntitySeedsHandler implements OperationHandler<GetAdjacentEntitySeeds, CloseableIterable<EntitySeed>> {

    @Override
    public CloseableIterable<EntitySeed> doOperation(final GetAdjacentEntitySeeds operation,
                                                     final Context context, final Store store)
            throws OperationException {
        return doOperation(operation, context.getUser(), (HBaseStore) store);
    }

    public CloseableIterable<EntitySeed> doOperation(final GetAdjacentEntitySeeds operation,
                                                     final User user,
                                                     final HBaseStore store)
            throws OperationException {
        if (null == operation.getSeeds()) {
            // If null seeds no results are returned
            return new WrappedCloseableIterable<>(Collections.emptyList());
        }

        operation.addOption(HBaseStoreConstants.OPERATION_RETURN_MATCHED_SEEDS_AS_EDGE_SOURCE, "true");
        operation.setIncludeEntities(false);
        if (IncludeEdgeType.NONE == operation.getIncludeEdges()) {
            operation.setIncludeEdges(IncludeEdgeType.ALL);
        }

        final HBaseRetriever<?> edgeRetriever;
        try {
            edgeRetriever = new HBaseRetriever<>(store, operation, user, operation.getSeeds());
        } catch (final StoreException e) {
            throw new OperationException(e.getMessage(), e);
        }

        return new ExtractDestinationEntitySeed(edgeRetriever);
    }

    private static final class ExtractDestinationEntitySeed extends TransformIterable<Element, EntitySeed> {
        private ExtractDestinationEntitySeed(final Iterable<Element> input) {
            super(input, new IsEdgeValidator());
        }

        @Override
        protected EntitySeed transform(final Element element) {
            return new EntitySeed(((Edge) element).getDestination());
        }

        @Override
        public void close() {
            ((CloseableIterable) super.getInput()).close();
        }
    }
}