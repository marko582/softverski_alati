ALTER TABLE workout_session_items ADD COLUMN set_weights_kg JSONB;

UPDATE workout_session_items AS wsi
SET set_weights_kg = (
    SELECT COALESCE(
            jsonb_agg(
                    CASE
                        WHEN gs.idx = 1 AND wsi.weight_kg IS NOT NULL THEN to_jsonb(wsi.weight_kg)
                        ELSE 'null'::jsonb
                        END
                    ORDER BY gs.idx
            ),
            '[]'::jsonb
           )
    FROM generate_series(1, wsi.sets_planned) AS gs(idx)
);

ALTER TABLE workout_session_items DROP COLUMN weight_kg;

ALTER TABLE workout_session_items ALTER COLUMN set_weights_kg SET NOT NULL;
