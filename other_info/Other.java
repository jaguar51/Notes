    private void getLinks() {
        thisLinks.clear();

        SQLiteDatabase sdb = notesDatabase.getReadableDatabase();
        Cursor cursor = sdb.query(
                NotesDatabaseHelper.TABLE_LINK,
                null,
                NotesDatabaseHelper.FIRST_ID + " = " + Integer.toString(this.noteID)
                        + " OR " + NotesDatabaseHelper.SECOND_ID + " = "
                        + Integer.toString(this.noteID),
                null, null, null, null
        );

        int idFirstNote = cursor.getColumnIndex(NotesDatabaseHelper.FIRST_ID);
        int idSecondNote = cursor.getColumnIndex(NotesDatabaseHelper.SECOND_ID);

        while (cursor.moveToNext()) {
            if (cursor.getInt(idFirstNote) == this.noteID)
                thisLinks.add(cursor.getInt(idSecondNote));

            if (cursor.getInt(idSecondNote) == this.noteID)
                thisLinks.add(cursor.getInt(idFirstNote));
        }

        cursor.close();
        sdb.close();
    }