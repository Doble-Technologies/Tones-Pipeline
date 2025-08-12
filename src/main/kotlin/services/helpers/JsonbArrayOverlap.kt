package tech.parkhurst.services.helpers

import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.QueryBuilder
import org.jetbrains.exposed.v1.core.append

fun jsonbArrayOverlap(columnName: String, values: List<Int>): Op<Boolean> {
    return object : Op<Boolean>() {
        override fun toQueryBuilder(queryBuilder: QueryBuilder) {
            queryBuilder.append(
                "EXISTS (SELECT * FROM jsonb_array_elements_text(",
                columnName,
                ") AS elem WHERE elem::int IN ("
            )
            values.forEachIndexed { idx, value ->
                if (idx > 0) queryBuilder.append(", ")
                queryBuilder.append(value.toString())
            }
            queryBuilder.append("))")
        }
    }
}