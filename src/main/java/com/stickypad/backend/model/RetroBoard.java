package com.stickypad.backend.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.stickypad.backend.exceptions.ForbiddenAccess;
import com.stickypad.backend.exceptions.NoteNotFound;
import com.stickypad.backend.exceptions.NoteUnChanged;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AccessLevel;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder(access = AccessLevel.PRIVATE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class RetroBoard {

  private final Integer id;
  private final String boardLink;
  private final String hostUserId;
  private final Map<String, UserStatus> userStatus;
  private final List<Note> notes;
  private Integer version;


  private enum UserStatus {
    in_progress,
    finished,
  }


  public static RetroBoard create(Integer boardId, String hostUserId, String appBaseUrl) {
    RetroBoard retroBoard = RetroBoard
      .builder()
      .id(boardId)
      .hostUserId(hostUserId)
      .boardLink(URI.create(appBaseUrl + "retro-board/" + boardId).toString())
      .userStatus(new ConcurrentHashMap<>()) // Using ConcurrentHashMap for thread-safe operations on user status
      .notes(new CopyOnWriteArrayList<>()) // Using CopyOnWriteArrayList for thread-safe operations on notes
      .version(1) // Initial version of the board, This should keep on increasing with every change
      .build();

    // Add the host user to status
    retroBoard.updateUserStatus(hostUserId, UserStatus.in_progress);
    return retroBoard;
  }


  public RetroBoard deleteNote(String noteId) {
    notes.stream()
      .filter(note -> note.id().equals(noteId))
      .findFirst()
      .ifPresentOrElse(note -> {
          notes.remove(note);
          updateBoardVersion(); // Update the board version after deleting a note
        },
        () -> { throw new NoteNotFound("Note with id " + noteId + " not found"); }
      );

    return this;
  }


  public RetroBoard updateNote(String noteId, String content, String userId) {
    Optional<Note> optionalNote = notes.stream()
      .filter(note -> note.id().equals(noteId))
      .findFirst();

    if (optionalNote.isEmpty()) {
      throw new NoteNotFound("Note with id " + noteId + " not found");
    }

    Note note = optionalNote.get();

    // Note owner should match the userId, (Other user should not be able to update the note)
    if (!note.userId().equals(userId)) {
      throw new ForbiddenAccess("Note with id " + noteId + " does not belong to user with id " + userId);
    }

    // Validate the content is not same
    if (note.content().equals(content)) {
      throw new NoteUnChanged("Note with id " + noteId + " is not modified");
    }

    // Update the note content [Since it is a record we can't mutate it, so we create a new instance]
    // But we need to maintain the same note ID as earlier
    Note newNote = note.getNewNote(content);
    notes.remove(note);
    notes.add(newNote);

    updateUserStatus(userId, UserStatus.in_progress);
    updateBoardVersion(); // Update the board version after deleting a note
    return this;
  }


  public boolean newVersionAvailable(Integer oldVersion) {
    return version > oldVersion;
  }


  private void updateUserStatus(String hostUserId, UserStatus userStatus) {
    this.userStatus.put(hostUserId, userStatus);
  }


  public RetroBoard addNote(Note note) {
    this.notes.add(note);
    updateUserStatus(note.userId(), UserStatus.in_progress); // Update user status to in_progress
    updateBoardVersion();
    return this;
  }


  private void updateBoardVersion() {
    this.version += 1;  // Increment the version of the board
  }


}
