/*
 * Copyright 2016-2017 Crown Copyright
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

package uk.gov.gchq.gaffer.hbasestore.coprocessor.processor;

import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.hbasestore.serialisation.ElementCell;
import java.util.Iterator;
import java.util.List;

public abstract class FilterProcessor implements GafferScannerProcessor {
    protected abstract boolean validate(final Element element);

    @Override
    public List<ElementCell> process(final List<ElementCell> elementCells) {
        final Iterator<ElementCell> itr = elementCells.iterator();
        while (itr.hasNext()) {
            final ElementCell elementCell = itr.next();
            if (!elementCell.isDeleted()) {
                final Element element = elementCell.getElement();
                if (!validate(element)) {
                    itr.remove();
                }
            }
        }

        return elementCells;
    }
}