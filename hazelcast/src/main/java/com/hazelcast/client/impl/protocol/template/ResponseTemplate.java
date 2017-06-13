/*
 * Copyright (c) 2008-2017, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.client.impl.protocol.template;

import com.hazelcast.annotation.ContainsNullable;
import com.hazelcast.annotation.GenerateCodec;
import com.hazelcast.annotation.Nullable;
import com.hazelcast.annotation.Response;
import com.hazelcast.annotation.Since;
import com.hazelcast.client.impl.client.DistributedObjectInfo;
import com.hazelcast.client.impl.protocol.constants.ResponseMessageConst;
import com.hazelcast.core.Member;
import com.hazelcast.map.impl.SimpleEntryView;
import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Client Protocol Responses
 */
@GenerateCodec(id = 0, name = "response", ns = "")
public interface ResponseTemplate {

    @Response(ResponseMessageConst.VOID)
    void Void();

    /**
     * @param response True if operation is successful, false otherwise.
     */
    @Response(ResponseMessageConst.BOOLEAN)
    void Boolean(boolean response);

    /**
     * @param response The operation result as an integer.
     */
    @Response(ResponseMessageConst.INTEGER)
    void Integer(int response);

    /**
     * @param response The operation result as a long.
     */
    @Response(ResponseMessageConst.LONG)
    void Long(long response);

    /**
     * @param response The operation result as a string.
     */
    @Response(ResponseMessageConst.STRING)
    void String(String response);

    /**
     * @param response The operation result as a serialized byte-array.
     */
    @Response(ResponseMessageConst.DATA)
    void Data(@Nullable Data response);

    /**
     * @param response The operation result as an array of serialized byte-array.
     */
    @Response(ResponseMessageConst.LIST_DATA)
    void ListData(List<Data> response);

    /**
     * @param response The operation result as an array of serialized byte-array that might have null entries.
     */
    @Response(ResponseMessageConst.LIST_DATA_MAYBE_NULL_ELEMENTS)
    void ListDataMaybeNullElements(@ContainsNullable List<Data> response);

    /**
     * @param response The operation result as an array of serialized key-value byte-arrays.
     */
    @Response(ResponseMessageConst.LIST_ENTRY)
    void ListEntry(List<Map.Entry<Data, Data>> response);

    /**
     * @param status               Authentication result as 0:AUTHENTICATED, 1:CREDENTIALS_FAILED, 2:SERIALIZATION_VERSION_MISMATCH
     * @param address              The address of the member server. This value will be null if status is not 0
     * @param uuid                 Unique string identifying the connected client uniquely. This string is generated by the owner member server
     *                             on initial connection. When the client connects to a non-owner member it sets this field on the request.
     *                             This value will be null if status is not 0
     * @param ownerUuid            Unique string identifying the server member uniquely.This value will be null if status is not 0
     * @param serializationVersion server side supported serialization version.
     *                             This value should be used for setting serialization version if status is 2
     * @param serverHazelcastVersion The hazelcast version of the member
     * @param clientUnregisteredMembers The list of members at which the client has no resources. This may be due to the client
     *                                  no connected to the cluster at all or it may be that the cleanup operation is executed at
     *                                  the member and no resources of the particular client is left at the member. The client
     *                                  can use this information to restore its needed resources at the member, e.g.
     *                                  registers its listeners. The list will be empty if this is response to non-owner
     *                                  connection request.
     * @return Returns the address, uuid and owner uuid.
     */
    @Response(ResponseMessageConst.AUTHENTICATION)
    Object Authentication(byte status, @Nullable Address address, @Nullable String uuid, @Nullable String ownerUuid,
                          byte serializationVersion, @Since(value = "1.3") String serverHazelcastVersion,
                          @Since(value = "1.3") @Nullable List<Member> clientUnregisteredMembers);

    /**
     * @param partitions mappings from member address to list of partition id 's that member owns
     */
    @Response(ResponseMessageConst.PARTITIONS)
    void Partitions(List<Map.Entry<Address, List<Integer>>> partitions);

    /**
     * @param response An list of DistributedObjectInfo (service name and object name).
     */
    @Response(ResponseMessageConst.LIST_DISTRIBUTED_OBJECT)
    void ListDistributedObject(List<DistributedObjectInfo> response);

    /**
     * @param response Response as an EntryView Data type.
     */
    @Response(ResponseMessageConst.ENTRY_VIEW)
    void EntryView(@Nullable SimpleEntryView<Data, Data> response);

    /**
     * @param jobPartitionStates The state of the job. See Job Partition State Data Type description for details.
     * @param processRecords     Number of processed records.
     * @return The information about the job if exists
     */
    @Response(ResponseMessageConst.JOB_PROCESS_INFO)
    Object JobProcessInfo(List<JobPartitionState> jobPartitionStates, int processRecords);

    /***
     * @param tableIndex the last tableIndex processed
     * @param keys       list of keys
     */
    @Response(ResponseMessageConst.CACHE_KEY_ITERATOR_RESULT)
    void CacheKeyIteratorResult(int tableIndex, List<Data> keys);

    /**
     * @param errorCode      error code of this exception
     * @param className      java class name of exception
     * @param message        details of exception
     * @param stacktrace     array of stack trace
     * @param causeErrorCode error code of cause of this exception, if there is no cause -1
     * @param causeClassName java class name of the cause of this exception
     */
    @Response(ResponseMessageConst.EXCEPTION)
    void Exception(int errorCode, String className, @Nullable String message, StackTraceElement[] stacktrace
            , int causeErrorCode, @Nullable String causeClassName);

    /**
     * @param readCount The number of items read from the ringbuffer. This can be different from
     *                  the size of the item array if a filter was applied when reading and some
     *                  items were skipped and are not in the returned array. This can also be
     *                  seen as the delta with which the user should increase his sequence
     *                  counter after consuming all of the returned items in the result set
     *                  to request the next set from the ringbuffer.
     * @param items     The array of serialized items.
     * @param itemSeqs  sequence IDs of returned ringbuffer items. This array can be {@code null}
     *                  if the cluster version is 3.8 or lower. If the cluster version is 3.9 or
     *                  higher then the array with sequence IDs will be sent. The array size is
     *                  equal to the size of the items array and the arrays have a one-to-one
     *                  mapping: the index of the sequence ID in the sequence array is equal
     *                  to the index of the item in the item array. These sequences can be used to
     *                  provide the user information to request a subset of the returned set if
     *                  the user has for some reason stopped processing working when processing
     *                  this returned set. If the ringbuffer is read without a filter then these
     *                  sequences are a contiguous range and the size of the arrays is equal to the
     *                  readCount.
     */
    @Response(ResponseMessageConst.READ_RESULT_SET)
    void ReadResultSet(int readCount, List<Data> items, @Since("1.5") @Nullable long[] itemSeqs);

    /**
     * @param tableIndex the last tableIndex processed,
     *                   it is used to act as a cursor to tell where should next batch read begin
     * @param entries    list of entries
     */
    @Response(ResponseMessageConst.ENTRIES_WITH_CURSOR)
    void EntriesWithCursor(int tableIndex, List<Map.Entry<Data, Data>> entries);

    @Since("1.4")
    @Response(ResponseMessageConst.SCHEDULED_TASK_STATISTICS)
    void ScheduledTaskStatistics(long lastIdleTimeNanos, long totalIdleTimeNanos,
                                 long totalRuns, long totalRunTimeNanos);

    @Since("1.4")
    @Response(ResponseMessageConst.ALL_SCHEDULED_TASK_HANDLERS)
    void AllScheduledTasksHandlers(List<Map.Entry<Member, List<ScheduledTaskHandler>>> handlers);

    /**
     * @param namePartitionSequenceList name to partition sequenceId mapping list
     * @param partitionUuidList         partitionId to UUID mapping list
     */
    @Since("1.4")
    @Response(ResponseMessageConst.NEAR_CACHE_INVALIDATION_META_DATA)
    void NearCacheInvalidationMetaData(List<Map.Entry<String, List<Map.Entry<Integer, Long>>>> namePartitionSequenceList, List<Map.Entry<Integer, UUID>> partitionUuidList);

    /**
     * @param partitionUuidList partitionId to UUID mapping list
     */
    @Since("1.4")
    @Response(ResponseMessageConst.LIST_ENTRY_PARTITION_UUID)
    void PartitionUuidList(List<Map.Entry<Integer, UUID>> partitionUuidList);

    /**
     * @param results                  The query results as an list of serialized projected entries that might have null entries
     * @param nextTableIndexToReadFrom the index from which new items can be fetched
     */
    @Since("1.5")
    @Response(ResponseMessageConst.QUERY_RESULT_SEGMENT)
    void ResultSegment(@ContainsNullable List<Data> results, int nextTableIndexToReadFrom);

    /**
     *
     * @param oldestSequence sequence ID of the oldest event in the event journal
     * @param newestSequence sequence ID of the newest event in the event journal
     */
    @Since("1.5")
    @Response(ResponseMessageConst.EVENT_JOURNAL_INITIAL_SUBSCRIBER_STATE)
    void EventJournalInitialSubscriberState(long oldestSequence, long newestSequence);
}
